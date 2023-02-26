import gurobipy as gp
from gurobipy import GRB

x = list(map(int, input().split()))

m = gp.Model()

y = m.addVar(vtype=GRB.BINARY)

N = len(x)

m.addConstr(sum(x) <= N * y)
m.addConstr(sum(x) >= N * y - N + 1)

m.optimize()
print(y.x)
