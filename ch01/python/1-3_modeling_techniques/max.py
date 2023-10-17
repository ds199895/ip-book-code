import gurobipy as gp
from gurobipy import GRB

m = gp.Model()

print("Enter two integers split with space:")
x, y = map(int, input().split())

z = m.addVar()

m.addConstr(x <= z)
m.addConstr(y <= z)
m.addConstr(3 <= z)

M = 100

u = m.addVars(3, vtype=GRB.BINARY)
m.addConstr(x >= z - M*(1-u[0]))
m.addConstr(y >= z - M*(1-u[1]))
m.addConstr(3 >= z - M*(1-u[2]))

m.addConstr(u[0] + u[1] +u[2]>= 1)

m.setObjective(z, GRB.MAXIMIZE)

m.optimize()
