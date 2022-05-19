import gurobipy as gp
from gurobipy import GRB

m = gp.Model()

# input
N = 5
pos = [[(1, 1), (2, 2), (1, 2)], [(2, 2), (1, 2), (3, 3)], [(3, 1), (2, 1)], [(3, 2), (2, 3)], [(1, 2), (3, 1), (1, 1)]]
c = [[2, 1, 3], [2, 3, 1], [1, 2], [3, 2], [1, 3, 2]]

# variables
x = [[] for _ in range(N)]

for i in range(N):
    for j in range(len(pos[i])):
        x[i].append(m.addVar(vtype=GRB.BINARY))    


# objective: total costs / constrant: one pos each city
obj = gp.LinExpr()
for i in range(N):
    constr = gp.LinExpr()
    for j in range(len(pos[i])):
        constr += x[i][j]
        obj += c[i][j] * x[i][j]
    m.addConstr(constr == 1)
m.setObjective(obj, GRB.MAXIMIZE)

# constraint: no overlap 
mp = {}
for i in range(N):
    for j in range(len(pos[i])):
        try:
            mp[pos[i][j]].append((i, j))
        except KeyError:
            mp[pos[i][j]] = []
            mp[pos[i][j]].append((i, j))

for key in mp.keys():
    if(len(mp[key]) > 1):
        constr = gp.LinExpr()
        for i, j in mp[key]:
            constr += x[i][j]
        m.addConstr(constr == 1)

# solve
m.optimize()

# output
print(m.ObjVal)

for i in range(N):
    for j in range(len(pos[i])):
        if(x[i][j].x == 1):
            print("City %d at pos (%d, %d)" % (i, pos[i][j][0], pos[i][j][1]))

# 11.0
# City 0 at pos (1, 2)
# City 1 at pos (2, 2)
# City 2 at pos (3, 1)
# City 3 at pos (3, 2)
# City 4 at pos (1, 1)
