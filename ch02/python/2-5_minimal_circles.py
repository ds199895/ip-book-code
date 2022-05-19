import gurobipy as gp
from gurobipy import GRB
import numpy as np
import matplotlib.pyplot as plt

count = 100
maxR = 100

circleNum = 5

xy = np.random.randint(0, maxR, [2,count]) 

m = gp.Model()

# Create variables

x = m.addVars(circleNum, lb=0, ub=maxR)
y = m.addVars(circleNum, lb=0, ub=maxR)
r = m.addVars(circleNum, lb=0, ub=maxR)
mxR = m.addVar(0, maxR)


# Set objective
m.setObjective(mxR, GRB.MINIMIZE)

M = maxR * maxR
# Add Constraints
for i in range(count):
    s = m.addVars(circleNum, vtype=GRB.BINARY)

    for j in range(circleNum):
        m.addConstr((xy[0][i] - x[j]) * (xy[0][i] - x[j]) + (xy[1][i] - y[j]) * (xy[1][i] - y[j]) <= r[j] * r[j] + M * (1 - s[j]))

    m.addConstr(gp.quicksum(s[i] for i in range(circleNum)) >= 1)

m.addGenConstrMax(mxR, r)

m.params.NonConvex = 2

m.optimize()



# Draw plot
fig = plt.figure(figsize=(10, 10))
ax = fig.add_subplot(1, 1, 1)

ax.scatter(xy[0], xy[1], color='k')

for i in range(circleNum):
    xx, yy, rr = x[i].x, y[i].x, r[i].x
    print('Circle (%f, %f), radius = %f' % (xx, yy, rr))

    ax.scatter(xx, yy, color='b')
    ax.add_artist(plt.Circle((xx, yy), rr, fill=False, color='b'))

plt.xlim(0-20, maxR+20)
plt.ylim(0-20, maxR+20)
fig.savefig('imgs/minimal-circles.png', bbox_inches='tight')
m.dispose()
plt.show()
