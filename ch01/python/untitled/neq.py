import gurobipy as gp
from gurobipy import GRB

m = gp.Model()

x1 = int(input())

x2 = m.addVar(lb=x1, vtype=GRB.INTEGER)

M = 0x3f3f3f3f
e = 0.001
y = m.addVar(vtype=GRB.BINARY)

m.addConstr(x1 - x2 <= -e + M * y)
m.addConstr(x1 - x2 >= e - M * (1-y))

m.setObjective(x2, GRB.MINIMIZE)
m.optimize()

print("x2: ", x2.x)
