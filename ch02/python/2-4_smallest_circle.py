import gurobipy as gp
from gurobipy import GRB
import numpy as np
import matplotlib.pyplot as plt

count = 100
maxR = 100

xy = np.random.randint(0, maxR, [2,count]) 

m = gp.Model()

# Create variables
x = m.addVar(0, maxR)
y = m.addVar(0, maxR)
r = m.addVar(0, maxR)

# Set objective
m.setObjective(r, GRB.MINIMIZE)

# Add Constraints
for i in range(count):
    m.addConstr((xy[0][i] - x) * (xy[0][i] - x) +
                (xy[1][i] - y) * (xy[1][i] - y) <=
                r * r)

m.params.NonConvex = 2

m.optimize()

xx, yy, rr = x.getAttr('x'), y.getAttr('x'), r.getAttr('x')

print('Circle (%f, %f), radius = %f' % (xx, yy, rr))

# Draw plot
fig = plt.figure(figsize=(10, 10))
ax = fig.add_subplot(1, 1, 1)

ax.scatter(xy[0], xy[1], color='k')
ax.scatter(xx, yy, color='b')
ax.add_artist(plt.Circle((xx, yy), rr, fill=False, color='b'))

plt.xlim(0-20, maxR+20)
plt.ylim(0-20, maxR+20)

m.dispose()
plt.show()
