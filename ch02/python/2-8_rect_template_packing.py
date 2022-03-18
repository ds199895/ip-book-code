import gurobipy as gp
from gurobipy import GRB
import matplotlib.pyplot as plt
from matplotlib.collections import PatchCollection


T = [(2, 3), (2, 3), (3, 4), (3, 1), (1, 1), (3, 2), (3, 3)]

D = (10, 10)

K = len(T)

m = gp.Model()

x = m.addVars(K, lb=1, ub=D[0])
y = m.addVars(K, lb=1, ub=D[1])


obj = gp.QuadExpr()
for i in range(K):
    for j in range(i):
        obj.add((x[j]-x[i])* (x[j]-x[i]))
        obj.add((y[j]-y[i])*(y[j]-y[i]))
        obj.add((x[j]-x[i] + T[j][0] - T[i][0]) * (x[j]-x[i] + T[j][0] - T[i][0]) )
        obj.add((y[j]-y[i] + T[j][1] - T[i][1]) * (y[j]-y[i] + T[j][1] - T[i][1]) )
m.setObjective(obj, GRB.MINIMIZE)

M = D[0] * D[1]


for i in range(K):
    m.addConstr(x[i] + T[i][0] <= D[0])
    m.addConstr(y[i] + T[i][1] <= D[1])


for i in range(K):
    for j in range(i):
        s = m.addVars(4, vtype=GRB.BINARY)
        m.addConstr(x[i] - T[j][0] >= x[j] - M * (1 - s[0]))
        m.addConstr(x[i] + T[i][0] <= x[j] + M * (1 - s[1]))
        m.addConstr(y[i] - T[j][1] >= y[j] - M * (1 - s[2]))
        m.addConstr(y[i] + T[i][1] <= y[j] + M * (1 - s[3]))
        m.addConstr(s[0]+s[1]+s[2]+s[3] >= 1)

m.optimize()

print ( m.SolCount)


for sol in range(m.SolCount):
    m.params.SolutionNumber = sol
    print('Current #%d: %f' % (sol, m.ObjVal))
    fig = plt.figure(figsize=(10, 10))
    ax = fig.add_subplot(1, 1, 1)

    for i in range(K):
        xx, yy= x[i].xn, y[i].xn
        area = T[i][0] * T[i][1]
        ax.add_artist(plt.Rectangle((xx, yy), T[i][0], T[i][1], facecolor="#C85B5B",
                    edgecolor="#BE9A9A", alpha=min(max(0.2, 1.3 - 0.08*area), 1)))
        print('Rectangle (%d, %d)' % ( xx, yy))

    plt.xlim(0, D[0] + 1)
    plt.ylim(0, D[1] + 1)

    plt.title('Rectangle Packing Result %d' % sol)
    print('Saving....')

    fig.savefig('imgs/packing/rect-template-packing-%d.png' % sol, bbox_inches='tight')
    # fig.clf()

m.dispose()
