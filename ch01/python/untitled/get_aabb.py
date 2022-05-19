import gurobipy as gp
from gurobipy import GRB

x1, y1, w1, h1 = map(int, input().split())
x2, y2, w2, h2 = map(int, input().split())


m = gp.Model()
x = m.addVar()
y = m.addVar()
w = m.addVar()
h = m.addVar()
m.addConstr(x == gp.min_(x1, x2))
m.addConstr(y == gp.min_(y1, y2))
m.addConstr(w == gp.max_(x1 + w1, x2 + w2))
m.addConstr(h == gp.max_(y1 + h1, y2 + h2))

m.optimize()

print(x.x, y.x, w.x, h.x)
