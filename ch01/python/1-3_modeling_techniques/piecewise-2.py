import gurobipy as gp
from gurobipy import GRB

m = gp.Model()

w = m.addVars(4, vtype=GRB.BINARY)
z = m.addVars(3, vtype=GRB.BINARY)



m.addConstr()
