# with open('D:/Documents/MQP/diva-maras/output_rules_SIDER.txt', 'r') as inFile:
# 	with open('D:/Documents/MQP/diva-maras/known_rules_SIDER.txt', 'w') as outFile:
# 		for line in inFile:
# 			values = line.split(",")
# 			if values[12] == "known":
# 				outFile.write(line)

# with open('D:/Documents/MQP/diva-maras/output_rules_Standardized_Known.txt', 'r') as inFile:
# 	with open('D:/Documents/MQP/diva-maras/known_rules_Standardized_Known.txt', 'w') as outFile:
# 		for line in inFile:
# 			values = line.split(",")
# 			if values[12] == "known":
# 				outFile.write(line)

# with open('D:/Documents/MQP/diva-maras/output_rules.txt', 'r') as inFile:
# 	with open('D:/Documents/MQP/diva-maras/known_rules_MetaMap.txt', 'w') as outFile:
# 		for line in inFile:
# 			values = line.split(",")
# 			if values[12] == "known":
# 				outFile.write(line)


allFound = True
countFound = 0
# with open('D:/Documents/MQP/diva-maras/known_rules_SIDER.txt', 'r') as siderFile:
# 	for line in siderFile:
with open('D:/Documents/MQP/diva-maras/known_rules_Standardized_Known.txt', 'r') as standardizedFile:
	for line in standardizedFile:
		values = line.split(",")
		ruleReaction = values[2]
		drugs = values[3].replace("[", "").replace("]", "").split(" ")
		ruleDrug1 = drugs[0]
		ruleDrug2 = drugs[1]
		# print(ruleId)
		found = False
		with open('D:/Documents/MQP/diva-maras/known_rules_MetaMap.txt', 'r') as metamapFile:
			for rule in metamapFile:
				metamapValues = rule.split(",")
				metamapRuleReaction = metamapValues[2]
				metamapDrugs = metamapValues[3].replace("[", "").replace("]", "").split(" ")
				metamapRuleDrug1 = metamapDrugs[0]
				metamapRuleDrug2 = metamapDrugs[1]
				if metamapRuleReaction == ruleReaction and ((metamapRuleDrug1 == ruleDrug1 and metamapRuleDrug2 == ruleDrug2) or (metamapRuleDrug1 == ruleDrug2 and metamapRuleDrug2 == ruleDrug1)):
					found = True
					countFound += 1
			if not found:
				print("Rule not found: ", line)
				allFound = False
if allFound:
	print("All rules found!")
print(countFound, " rules found!")
				