import matplotlib.pyplot as plt
import networkx as nx

import gurobipy as gp
from gurobipy import GRB

m = gp.Model()

# Martin's model(1991)

# input

G = nx.read_weighted_edgelist("example_graph.txt", nodetype = int)
print(G.nodes())

N = G.number_of_nodes()
M = G.number_of_edges()

x = m.addVars(M, vtype=GRB.BINARY)
y = m.addVars(N, N, N, vtype=GRB.BINARY)

# objective
obj = gp.LinExpr()
for i, e in enumerate( G.edges(data = True)):
    e[2]['id'] = i
    obj += x[i] * e[2]['weight']
    
print(G.edges(data = True))
m.setObjective(obj, GRB.MINIMIZE)


# constraint
sum = 0
for i in range(M):
    sum += x[i]
m.addConstr(sum == N - 1)

for u, v, data in G.edges(data = True):
    i = data['id']
    for k in range(N):
        m.addConstr(y[k, u, v] + y[k, v, u] == x[i])
    
for k in range(N):
    for u in range(N):
        if(u == k):
            continue
        sum = gp.LinExpr()
        for v in range(N):
            if(v == u):
                continue
            sum += y[k, u, v]
        m.addConstr(sum <= 1)

for k in range(N):
    sum = gp.LinExpr()
    for i in range(N):
        if(k == i):
            continue
        sum += y[k, k, i]
    m.addConstr(sum <= 0)

m.optimize()

edges = []
for i, e in enumerate( G.edges(data = True)):
    if(x[i].x == 1):
        edges.append((e[0], e[1]))

H = G.edge_subgraph(edges)
# output
pos = nx.spring_layout(G)
nx.draw_networkx(G, pos)
weights = nx.get_edge_attributes(G,'weight')
nx.draw_networkx_edge_labels(G, pos, edge_labels=weights)

options = {
    "node_color": "#5D99D4",
    "edgecolors": "black",
    "linewidths": 1,
    "width": 3,
    "edge_color": "#5D99D4",
    "alpha": 0.5
}
nx.draw_networkx(H, pos, **options)


plt.axis('off')
plt.show()
