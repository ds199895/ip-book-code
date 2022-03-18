# 在长w、宽d的矩形中，用数量一定、半径\geq r_{min}、互不相交的圆尽可能多地占据矩形内部

import gurobipy as gp
from gurobipy import GRB
import matplotlib.pyplot as plt
import math

min = (0, 0)
max = (15, 6)
circleNum = 10
rMin = 1

m = gp.Model()

# Create variables
x = m.addMVar(10, min[0], max[0])
y = m.addMVar(10, min[1], max[1])
r = m.addMVar(10, rMin)

# Set objective
S = (max[0]-min[0]) * (max[1]-min[1])
m.setObjective(S-r@r*math.pi)

# Add constraint
for i in range(circleNum):
    m.addConstr(x[i]-r[i] >= min[0])
    m.addConstr(y[i]-r[i] >= min[1])
    m.addConstr(x[i]+r[i] <= max[0])
    m.addConstr(y[i]+r[i] <= max[1])

xd = x.tolist()
yd = y.tolist()
rd = r.tolist()

for i in range(circleNum):
    for j in range(i):
        m.addQConstr((xd[i]-xd[j]) * (xd[i]-xd[j]) + (yd[i]-yd[j])
                     * (yd[i]-yd[j]) >= (rd[i]+rd[j]) * (rd[i]+rd[j]))

m.params.NonConvex = 2
m.params.TimeLimit = 20

m.optimize()

circles = []
area = 0
for i in range(circleNum):
    xx = x[i].getAttr("x")[0]
    yy = y[i].getAttr("x")[0]
    rr = r[i].getAttr("x")[0]
    area += rr * rr * math.pi
    circles.append(plt.Circle(
        (xx, yy), rr, color='#C85B5B', alpha=1.5 - 0.5*rr))
    print("{} {} {}".format(xx, yy, rr))


print(m.ObjVal)

fig = plt.figure(figsize=(15, 6))
ax = fig.add_subplot(1, 1, 1)

for i in range(circleNum):
    ax.add_patch(circles[i])

plt.title('Circle Packing with circle area = %.2f, occupied %.2f%s, time limit = %.0fs' % (
    area, area/S * 100, '%', m.params.TimeLimit))

plt.xlim(min[0], max[0])
plt.ylim(min[1], max[1])
fig.savefig('imgs/circle-packing-%.0f.png' %
            m.params.TimeLimit, bbox_inches='tight')


m.dispose()
