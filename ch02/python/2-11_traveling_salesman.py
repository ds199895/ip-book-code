import matplotlib.pyplot as plt
import networkx as nx
import itertools

import gurobipy as gp
from gurobipy import GRB

m = gp.Model()


# input

G = nx.read_weighted_edgelist("example_graph.txt", nodetype = int)

N = G.number_of_nodes()
M = G.number_of_edges()

x = m.addVars(M, vtype=GRB.BINARY)

# objective
obj = gp.LinExpr()
for i, e in enumerate( G.edges(data = True)):
    e[2]['id'] = i
    obj += x[i] * e[2]['weight']
    
print(G.edges(data = True))
m.setObjective(obj, GRB.MINIMIZE)

# constraint
for i in range(N):
    sum = 0
    for j in G.neighbors(i):
        id = G.edges[i, j]['id']
        print(id)
        sum += x[id]
    m.addConstr(sum == 2)


for r in range(2, N):
    for subset in itertools.combinations(G.nodes(), r):
        
        sum = gp.LinExpr()
        H = G.subgraph(subset)
        for e in H.edges(data=True):
            i = e[2]['id']
            sum += x[i]
            
        m.addConstr(sum <= H.number_of_nodes() - 1)

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
