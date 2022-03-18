import gurobipy as gp
from gurobipy import GRB

m = gp.Model()

x1 = m.addVar(vtype=GRB.INTEGER, lb=0, name="x1")
x2 = m.addVar(vtype=GRB.INTEGER, lb=0, name="x2")

m.addConstr(2 * x1 + 3 * x2 <= 14)
m.addConstr(4 * x1 + 2 * x2 <= 18)

m.setObjective(3 * x1 + 2 * x2, GRB.MAXIMIZE)

m.optimize()

for v in m.getVars():
    print('%s %g' % (v.VarName, v.X))

print('Obj: %g' % m.ObjVal)
