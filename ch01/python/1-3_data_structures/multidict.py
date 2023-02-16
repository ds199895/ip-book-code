import gurobipy as gp

# `multidict` is use for creating one or more dictionaries with same key in a single statement.
keys, a, b, c = gp.multidict({
    'x': [0, 1, 2], 
    'y': [1, 2, 5], 
    'z': [0, 3, 5]
    })

print(keys)
print(a)
print(b)
print(c)

# If the key of multidict is a tuple, it will be converted to a `tuplelist`
arcs, capacity = gp.multidict({
    ('Detroit', 'Boston'): 100,
    ('Detroit', 'New York'): 80,
    ('Detroit', 'Seattle'): 120,
    ('Denver', 'Boston'): 120,
    ('Denver', 'New York'): 120,
    ('Denver', 'Seattle'): 120
})

# The `arcs` is a `tuplelist`, which allows for selection from specific nodes.
for arc in arcs.select('*', 'Boston'):
    print(arc, ':', capacity[arc])
