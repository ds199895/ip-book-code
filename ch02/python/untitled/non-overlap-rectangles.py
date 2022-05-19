import gurobipy as gp
from gurobipy import GRB

m = gp.Model()

x, y, w, h = map(int, input().split())


rx = m.addVar()
ry = m.addVar()

s = m.addVars(4, vtype=GRB.BINARY)
M = 0x3f3f3f3f
m.addConstr(x + w <= rx + M * (1 - s[0]))
m.addConstr(y + h <= ry + M * (1 - s[1]))
m.addConstr(rx + w <= x + M * (1 - s[2]))
m.addConstr(ry + h <= y + M * (1 - s[3]))
m.addConstr(s[0] + s[1] + s[2] + s[3] >= 1)

mnx = m.addVar()
mny = m.addVar()
m.addConstr(mnx == gp.min_(x, rx))
m.addConstr(mny == gp.min_(y, ry))


m.setObjective((rx+x - mnx*2 + w) * (ry+y - mny*2 + h))

m.params.nonConvex = 2
m.optimize()
