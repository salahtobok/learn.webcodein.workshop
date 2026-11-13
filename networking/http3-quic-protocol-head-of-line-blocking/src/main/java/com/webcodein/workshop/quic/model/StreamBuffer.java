package com.webcodein.workshop.quic.model;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Reassembles contiguous stream bytes up to the expected read offset.
 */
public class StreamBuffer {

    private final long streamId;
    private final Map<Long, StreamFrame> bufferedFrames = new ConcurrentSkipListMap<>(Comparator.naturalOrder());
    private long nextExpectedOffset = 0L;
    private boolean streamFinished = false;

    public StreamBuffer(long streamId) {
        this.streamId = streamId;
    }

    public synchronized void pushFrame(StreamFrame frame) {
        if (frame.streamId() != streamId) {
            throw new IllegalArgumentException("Frame stream ID mismatch");
        }
        if (frame.offset() >= nextExpectedOffset) {
            bufferedFrames.put(frame.offset(), frame);
        }
    }

    public synchronized byte[] drainContiguousBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (bufferedFrames.containsKey(nextExpectedOffset)) {
            StreamFrame frame = bufferedFrames.remove(nextExpectedOffset);
            byte[] payload = frame.payload();
            out.writeBytes(payload);
            nextExpectedOffset += payload.length;
            if (frame.fin()) {
                streamFinished = true;
            }
        }
        return out.toByteArray();
    }

    public long streamId() {
        return streamId;
    }

    public synchronized long nextExpectedOffset() {
        return nextExpectedOffset;
    }

    public synchronized boolean isStreamFinished() {
        return streamFinished && bufferedFrames.isEmpty();
    }

    public synchronized int bufferedFramesCount() {
        return bufferedFrames.size();
    }
}
