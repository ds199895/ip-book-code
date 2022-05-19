from gurobipy import GRB
import gurobipy as gp
import matplotlib.pyplot as plt
import networkx as nx
import itertools

m = gp.Model()

# input

G = nx.read_edgelist("city_graph.txt", nodetype = int,data=(("weight", float),("time", float)),)

N = G.number_of_nodes()
M = G.number_of_edges()

s, t, T = 0, 6, 30

# variables
x = m.addVars(M, vtype=GRB.BINARY)
v = m.addVars(N, vtype=GRB.BINARY)

# objective
obj = 0
for i, e in enumerate( G.edges(data = True)):
    e[2]['id'] = i
    obj += x[i] * e[2]['weight']
m.setObjective(obj, GRB.MAXIMIZE)

# constraint
# time
sum = 0
for e in G.edges(data = True):
    i = e[2]['id']
    sum += e[2]['time'] * x[i]
m.addConstr(sum <= T)

# connect
for i in range(N):
    sum = 0
    for j in G.neighbors(i):
        id = G.edges[i, j]['id']
        sum += x[id]
    if(i != s and i != t):
        m.addConstr(sum == 2*v[i])

sum_start = 0
for i in G.neighbors(s):
    print(G.edges[i, s]['id'])
    id = G.edges[i, s]['id']
    sum_start += x[id] 
m.addConstr(sum_start == 1)
    
sum_end = 0
for i in G.neighbors(t):
    id = G.edges[i, t]['id']
    sum_end += x[id] 
m.addConstr(sum_end == 1)

# solve
m.optimize()

# output

edges = []
total_time = 0
for i, e in enumerate( G.edges(data = True)):
    if(x[i].x == 1):
        edges.append((e[0], e[1]))
        total_time += e[2]['time']

print("Best value: ", m.ObjVal)
print("Total time: ", total_time)
print("Select nodes:", end=" ")
for i in range(N):
    if(v[i].x == 1):
        print(i, end=" ")

H = G.edge_subgraph(edges)

pos = nx.spring_layout(G)
nx.draw_networkx(G, pos, node_color="#F4B183", edgecolors="black")
weights = nx.get_edge_attributes(G,'weight')
for e in weights:
    time = G.edges[e]['time']
    weight = G.edges[e]['weight']
    weights[e] = str(time) + '(' + str(weight)+')'

nx.draw_networkx_edge_labels(G, pos, edge_labels=weights)

options = {
    "node_color": "#EF5539",
    "edgecolors": "black",
    "linewidths": 1,
    "width": 3,
    "edge_color": "#EF5539",
    "alpha": 0.6
}
nx.draw_networkx(H, pos, **options)

plt.axis('off')
plt.show()

# Best value:  21.0
# Total time:  25.0
# Select nodes: 1 2 3 5 7
