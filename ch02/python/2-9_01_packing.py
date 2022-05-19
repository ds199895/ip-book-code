import matplotlib.pyplot as plt
import random
import gurobipy as gp
from gurobipy import GRB

m = gp.Model()


# input
types = [
    [(0, 0), (1, 0), (2, 0), (3, 0)],
    [(0, 0), (1, 0), (2, 0), (2, 1)],
    [(0, 0), (1, 0), (1, 1), (2, 1)],
    [(0, 0), (1, 0), (1, 1), (2, 0)]
]

colors = ["#ed6a5a", "#f4f1bb", "#9bc1bc", "#006e90"]

RX, RY = (13, 11)

# variables

x = m.addVars(RX, RY, len(types), vtype=GRB.BINARY)

# objective
obj = gp.LinExpr()
for i in range(RX):
    for j in range(RY):
        for k in range(len(types)):
            obj += x[i, j, k] * len(types[k])

m.setObjective(obj, GRB.MAXIMIZE)


# constraints
for i in range(RX):
    for j in range(RY):
        overlap = gp.LinExpr()
        for k in range(len(types)):
            for u, v in types[k]:
                if(i-u >= 0 and j-v >= 0):
                    overlap += x[i-u, j-v, k]

                if(i+u >= RX or j+v >= RY):
                    m.addConstr(x[i,j,k] == 0)

        m.addConstr(overlap <= 1) 



# optimize
m.Params.MIPGap = 0.001
m.optimize()


# output
def draw_rect(i, j, k):
    alpha = random.random()*0.5 + 0.5
    for u, v in types[k]:
        ax.add_artist(plt.Rectangle((i+u, j+v), 1, 1, facecolor=colors[k], edgecolor='k', linewidth=2, alpha=alpha))

fig = plt.figure(figsize=(10, 10))
ax = fig.add_subplot(1, 1, 1)

ax.set_xticks(range(0, RX+1, 1))
ax.set_yticks(range(0, RY+1, 1))
ax.grid()

for i in range(RX):
    for j in range(RY):
        for k in range(len(types)):
            if(x[i, j, k].x == 1):
                draw_rect(i, j, k)
                print(i, j, k)

plt.show()


