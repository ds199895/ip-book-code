# 在给定数量的三维随机点中，求与所有已知点距离平方之和最近的中心点

import gurobipy as gp
from gurobipy import GRB
import matplotlib.pyplot as plt
import numpy as np

count = 100
maxR = 100

xyz = np.random.randint(0, maxR, [3,count]) 


m = gp.Model()

x = m.addVar(0, maxR)
y = m.addVar(0, maxR)
z = m.addVar(0, maxR)


obj = gp.QuadExpr()
for i in range(count):
    obj.add( (x-xyz[0][i]) * (x-xyz[0][i]) + 
    (y-xyz[1][i]) * (y-xyz[1][i]) +
    (z-xyz[2][i]) * (z-xyz[2][i]))
m.setObjective( obj )

m.optimize()

xx, yy, zz = x.getAttr('x'), y.getAttr('x'), z.getAttr('x')

print('Minimal distance: (%.4f, %.4f, %.4f)' % (xx, yy, zz))
print('Center (%.4f, %.4f, %.4f)' % (xyz[0].mean(), xyz[1].mean(), xyz[2].mean()))

# Draw plot
fig = plt.figure()
ax = plt.axes(projection='3d')

ax.scatter3D(xyz[0], xyz[1], xyz[2], cmap='Blues')
ax.scatter3D(xx, yy, zz, color='r')
ax.scatter3D(xyz[0].mean(), xyz[1].mean(), xyz[2].mean(), color='g')
plt.show()
