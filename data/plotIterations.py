import matplotlib.pyplot as plt

file = open('C:\\work\\git\\AutoRota\\test.log', 'r')
strs = file.read().split("\n")
nums = [int(s) for s in strs]

test = 0
plt.plot(nums)
plt.show()