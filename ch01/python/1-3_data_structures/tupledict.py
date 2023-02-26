import gurobipy as gp

# The input list only accepts tuples of length 2.
l = gp.tupledict([('a', 2), ('b', 3), ('c', 3), ('d', 1)])

print(l.select('a', '*'))

# Rather than `sum(l.select('*'))` you can use l.sum('*') instead.
print(sum(l.select(['a', 'b'], '*')))
print(l.sum(['a', 'b'], '*'))


