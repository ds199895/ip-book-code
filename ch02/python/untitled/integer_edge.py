import gurobipy as gp
from gurobipy import GRB
import math


polygon = [(0, 0), (4.5, 0), (5, 4), (2, 5)]
N = len(polygon)

m = gp.Model()

r = m.addVars(4, vtype=GRB.INTEGER)
dx = m.addVars(4)
dy = m.addVars(4)

for i in range(N):
    x1, y1 = polygon[i]
    x2, y2 = polygon[(i + 1) % N]
    x1 += dx[i]
    y1 += dy[i]
    x2 += dx[(i + 1) % N]
    y2 += dy[(i + 1) % N]

    m.addConstr( (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) == r[i] * r[i])


m.setObjective(gp.quicksum(dx)+gp.quicksum(dy), GRB.MINIMIZE)

m.params.NonConvex = 2
m.optimize()

xs = []
ys = []
for i in range(N):
    x, y = polygon[i]
    xs.append(x + dx[i].x)
    ys.append(y + dy[i].x)
    print("Pos: ", x + dx[i].x, y + dy[i].x)
xs.append(xs[0])
ys.append(ys[0])

for i in range(N):
    l = math.sqrt((xs[i] - xs[i+1])**2 + (ys[i] - ys[i+1])**2)
    print("Len: ", i, l)

xr, yr = map(list, zip(*polygon))
xr.append(xr[0])
yr.append(yr[0])

import matplotlib.pyplot as plt
plt.figure()
plt.plot(xs, ys, color='b') 
plt.plot(xr, yr, color='r')

plt.show()


