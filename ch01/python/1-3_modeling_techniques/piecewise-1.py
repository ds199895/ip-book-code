import gurobipy as gp
from gurobipy import GRB

eps = 10e-3

m = gp.Model()

x = int(input())

l = m.addVars(3, vtype=GRB.BINARY)

M = 0x3f3f3f3f

m.addConstr(x <= 100 * l[0] + 500 * l[1] + M * l[2])
m.addConstr(x >= 100 * l[1] + 500 * l[2] + eps)
m.addConstr(l[0] + l[1] + l[2] == 1)


m.optimize()

y = l[0].x + 0.9 * l[1].x + 0.8 * l[2].x

print(y)

