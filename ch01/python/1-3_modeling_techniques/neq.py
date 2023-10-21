import gurobipy as gp
from gurobipy import GRB

m = gp.Model()

print("Enter x1:")
x1 = float(input())

x2 = m.addVar(lb=x1, vtype=GRB.CONTINUOUS)

M = 100
e = 0.0001
y = m.addVar(vtype=GRB.BINARY)

# |x1 - x2| >= e
# x1-x2 >= e - M * y
# x1-x2 <= -e + M * (1-y)

m.addConstr(x1 - x2 <= -e + M * y)
m.addConstr(x1 - x2 >= e - M * (1-y))

# Set the objective to minimize x2 with a lower bound of x1 to check if the condition |x1 - x2| >= e is met.
m.setObjective(x2, GRB.MINIMIZE)
m.optimize()

print("x2: ", x2.x)
print("y: ", y.x)
