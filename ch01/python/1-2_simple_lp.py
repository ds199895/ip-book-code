import gurobipy as gp
from gurobipy import GRB

m = gp.Model()

x1 = m.addVar(vtype=GRB.CONTINUOUS, lb=0, name="x1")
x2 = m.addVar(vtype=GRB.CONTINUOUS, lb=0, name="x2")
x3 = m.addVar(vtype=GRB.CONTINUOUS, lb=0, name="x3")
x4 = m.addVar(vtype=GRB.CONTINUOUS, lb=0, name="x4")
x5 = m.addVar(vtype=GRB.CONTINUOUS, lb=0, name="x5")

m.addConstr(3*x1 + 9*x2 + x3 == 540)
m.addConstr(5*x1 + 5*x2 + x4 == 450)
m.addConstr(9*x1 + 3*x2 + x5 == 720)

m.setObjective(70*x1 + 30*x2, GRB.MAXIMIZE)

m.optimize()
print(m.objVal)
print(x1.x, x2.x, x3.x, x4.x, x5.x)
