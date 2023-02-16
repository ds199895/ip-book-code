import gurobipy as gp

# `tuplelist` is a subclass of `list`
l = gp.tuplelist([(1, 2), (1, 3), (2, 3), (3, 1)])

# in which you can use `select` method to specify the desired values
print(l.select('*', 3))

print(l.select(1, '*')) # equal to: 
print([(x, y) for x, y in l if x == 1])

# use list to select multiple values
print(l.select('*', [1, 2]))

# operator of `list` is also allowed as tuplelist is the subclass of `list`
l += [(3, 4)]
