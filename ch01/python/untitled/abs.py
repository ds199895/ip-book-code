import gurobipy as gp
from gurobipy import GRB

x = int(input())
c = 10

m = gp.Model()

y = m.addVar()

m.setObjective(c*y, GRB.MINIMIZE)
m.addConstr(y >= x)
m.addConstr(y >= -x)

m.optimize()

print(m.ObjVal)
