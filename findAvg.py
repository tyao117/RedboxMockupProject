import sys
sum = 0
n = 0 

for x in sys.stdin:
    n += 1
    sum += int(x)

print("sum="+str(sum))
print("n="+str(n))