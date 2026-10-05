local inventory_key = KEYS[1]
local requested_quantity = tonumber(ARGV[1])
local current_inventory = tonumber(redis.call('get', inventory_key) or "0")
if current_inventory >= requested_quantity then
    redis.call('decrby', inventory_key, requested_quantity)
    return 1
else
    return 0
end
