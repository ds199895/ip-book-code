import gurobipy as gp
from gurobipy import GRB

x = list(map(int, input().split()))

m = gp.Model()

y = m.addVar(vtype=GRB.BINARY)

N = len(x)

t = m.addVar(vtype=GRB.INTEGER)

m.addConstr(y == sum(x) - 2*t)
m.addConstr(y >= 0)

m.optimize()
print(y.x)
