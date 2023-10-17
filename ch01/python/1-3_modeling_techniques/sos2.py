import gurobipy as gp
from gurobipy import GRB

x = [1, 2, 4, 5]

m = gp.Model()

y = m.addVars(len(x), vtype=GRB.BINARY)

m.setObjective(y.prod(x), GRB.MAXIMIZE)
# m.addSOS(GRB.SOS_TYPE2, y)

def addSOS2():
    vs = m.addVars(len(x)-1, vtype=GRB.INTEGER)
    for i in range(len(x)-1):
        m.addConstr(vs[i] == y[i] + y[i+1])

    m.addConstr(max(vs) == y.sum())

addSOS2()

m.optimize()

for i in range(len(x)):
    print(y[i].x)

