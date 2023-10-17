import gurobipy as gp
from gurobipy import GRB

x = [1, 2, 4, 5]

m = gp.Model()

y = m.addVars(len(x), vtype=GRB.BINARY)


m.setObjective(y.prod(x), GRB.MAXIMIZE)
m.addSOS(GRB.SOS_TYPE1, y)
# m.addConstr(y.sum() <= 1)

m.optimize()

for i in range(len(x)):
    print(y[i].x)
