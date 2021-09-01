local res = http.get("https://gitlab.com/SirEdvin/dark_toolkit/-/raw/master/libs/testsuite.lua")
local data = res.readAll()
local testsuite = load(data, "@testsuite.lua", nil, _ENV)()
testsuite.mixToFramework(test, test.fail)
