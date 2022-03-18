import gurobipy as gp
from gurobipy import GRB

m = gp.Model()


# input
c = 10
w = [5, 8, 3]
v = [3, 7, 5]

# variables
x = m.addVars(len(w), vtype=GRB.BINARY)


# objective: maximize value | constraint: not exceed capacity
obj = 0
cons = 0
for i in range(len(w)):
    obj += x[i] * v[i]
    cons += x[i] * w[i]
    
m.setObjective(obj, GRB.MAXIMIZE)
m.addConstr(cons <= c)

# solve
m.optimize()

# output
print('Sovled with maximum weight = %d' % m.ObjVal)
for i in range(len(w)):
    if(x[i].x == 1):
        print('Item %d: w = %.0f, v = %.0f' % (i+1, w[i], v[i]))

# Sovled with maximum weight = 8
# Item 1: w = 5, v = 3
# Item 3: w = 3, v = 5
