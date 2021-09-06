local label = os.getComputerLabel()
if label == nil then return test.fail("Label a computer to use it.") end

label = string.gsub(label, "_.+", "")

test.log("Loading file")
local fn, err = loadfile("tests/" .. label .. ".lua", nil, _ENV)
if not fn then return test.fail(err) end

test.log("Loading calling function")
local ok, err = pcall(fn)
if not ok then return test.fail(err) end

print("Run " .. label)

test.log("Finishing test")
test.ok()

test.log("Point after finish")
