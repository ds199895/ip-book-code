import gurobipy as gp
from gurobipy import GRB
import matplotlib.pyplot as plt
from matplotlib.collections import PatchCollection
import math

rectNum = 10
minW, minD = 1.5, 1.2
maxW, maxD = 3, 4
start, W, D = (0, 0), 15, 6
obstacles = [(0, 0, 7.2, 1.5), (6, 3, 3, 1.5), (12, 2, 3, 4)]  # (x, y, w, d)

# rectNum = 6 
# minW, minD = 0,  0
# maxW, maxD = 100, 100

m = gp.Model()

# Add variables
x = m.addVars(rectNum, lb=start[0], ub=start[0]+W)
y = m.addVars(rectNum, lb=start[1], ub=start[1]+D)
w = m.addVars(rectNum, lb=minW, ub=maxW)
d = m.addVars(rectNum, lb=minD, ub=maxD)


# Set objective
S = W * D - sum(o[2] * o[3] for o in obstacles)
print(S)
m.setObjective(S - gp.quicksum(w[i]*d[i] for i in range(rectNum)))

# Add Constraints
m.addConstrs(x[i] + w[i] <= start[0] + W for i in range(rectNum))
m.addConstrs(y[i] + d[i] <= start[1] + D for i in range(rectNum))

M = W * D

for i in range(rectNum):
    for j in range(i):
        s = m.addVars(4, vtype=GRB.BINARY)
        m.addConstr(x[i] - w[j] >= x[j] - M * (1 - s[0]))
        m.addConstr(x[i] + w[i] <= x[j] + M * (1 - s[1]))
        m.addConstr(y[i] - d[j] >= y[j] - M * (1 - s[2]))
        m.addConstr(y[i] + d[i] <= y[j] + M * (1 - s[3]))
        m.addConstr(s[0]+s[1]+s[2]+s[3] >= 1)
    for o in obstacles:
        s = m.addVars(4, vtype=GRB.BINARY)
        m.addConstr(x[i] - o[2] >= o[0] - M * (1 - s[0]))
        m.addConstr(x[i] + w[i] <= o[0] + M * (1 - s[1]))
        m.addConstr(y[i] - o[3] >= o[1] - M * (1 - s[2]))
        m.addConstr(y[i] + d[i] <= o[1] + M * (1 - s[3]))
        m.addConstr(s[0]+s[1]+s[2]+s[3] >= 1)

m.params.NonConvex = 2
m.params.TimeLimit = 20

m.optimize()


# Draw plot

fig = plt.figure(figsize=(15, 6))
ax = fig.add_subplot(1, 1, 1)

for i in range(rectNum):
    area = w[i].x * d[i].x
    ax.add_artist(plt.Rectangle((x[i].x, y[i].x), w[i].x, d[i].x, facecolor="#C85B5B",
                  edgecolor="#BE9A9A", alpha=min(max(0.2, 1.3 - 0.08*area), 1)))
    print('Rectangle %d (%.2f, %.2f) w = %.2f d = %.2f' %
          (i, x[i].x, y[i].x, w[i].x, d[i].x))
# ax.add_artist(plt.Rectangle(start, W, D))

for o in obstacles:
    ax.add_artist(plt.Rectangle(
        (o[0], o[1]), o[2], o[3], facecolor="white", edgecolor="black"))

plt.xlim(start[0], start[0]+W)
plt.ylim(start[1], start[1]+D)

plt.title('Rectangle Packing with area = %.2f, occupied %.2f%s, time limit = %.0fs' % (
    S-m.ObjVal, (S-m.ObjVal)/S * 100, '%', m.params.TimeLimit))
fig.savefig('imgs/rect-packing-%.0f-2.png' %
            m.params.TimeLimit, bbox_inches='tight')
m.dispose()
plt.show()
