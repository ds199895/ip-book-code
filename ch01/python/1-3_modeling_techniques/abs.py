import gurobipy as gp
from gurobipy import GRB

print("Enter a negative integer represent x:")
x = int(input())

print("Enter a positive integer represent c:")
c = int(input())

m = gp.Model()

y = m.addVar()

m.setObjective(c*y, GRB.MINIMIZE)
m.addConstr(y >= x)
m.addConstr(y >= -x)

m.optimize()

print(m.ObjVal)
