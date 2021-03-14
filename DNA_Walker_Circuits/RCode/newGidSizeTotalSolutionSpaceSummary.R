
options(scipen=999)
data <- read.table("E:\\TestDavidSolutions\\newGridSize\\Toy2\\toy2_candl_swipl.txt",
                   header = F)

test<- data[,c(1, ncol(data) - 4, ncol(data) - 2, ncol(data))]

colnames(test) <- c("Layout","Leaks","Area","Fitness")

write.table(test$V1, "E:\\TestDavidSolutions\\newGridSize\\Toy\\toy_14X14.txt",
            sep = "\n", row.names = F, col.names = F, quote = F)

#process the fitness column
test$Fitness <- lapply(test$Fitness, function(x) gsub("fitness=","",x))

#process the area column
test$Area <- lapply(test$Area, function(x) gsub("area=","",x))

#process the area column
test$Leaks <- lapply(test$Leaks, function(x) gsub("leaks=\\[","",x))
test$Leaks <- lapply(test$Leaks, function(x) gsub("\\]","",x))
# 
# test<- data[,c("V1", "V51", "V53", "V55")]
# 
# write.table(test$V1, "E:\\TestDavidSolutions\\newGridSize\\Toy\\toy_14X14.txt",
#             sep = "\n", row.names = F, col.names = F, quote = F)
# 
# #process the fitness column
# test$V55 <- lapply(test$V55, function(x) gsub("fitness=","",x))
# 
# #process the area column
# test$V53 <- lapply(test$V53, function(x) gsub("area=","",x))
# 
# #process the area column
# test$V51 <- lapply(test$V51, function(x) gsub("leaks=\\[","",x))
# test$V51 <- lapply(test$V51, function(x) gsub("\\]","",x))

fitnessFrequency = test[,c(1,4)]

fitnessFrequency$Fitness = as.numeric(fitnessFrequency$Fitness)

Freq= as.data.frame(table(fitnessFrequency$Fitness))
colnames(Freq)<- c("Fitness","Frequency")


write.table(Freq, "E:\\TestDavidSolutions\\newGridSize\\Toy0\\toy0_14X14_fitFreq.txt",
            sep = ",", row.names = F, quote = F)

Freq[c(1:14),]

library(ggplot2)

level_order <- sort(Freq$Fitness)

p <- ggplot(data=Freq, aes(x= as.factor(Fitness), y=Frequency)) +
  geom_bar(stat="identity", fill="steelblue")+
  theme_minimal() +
  #ggtitle("Toy layout - Fitness Distribution") +
  xlab("Fitness Values") +
  ylab("Frequency") +
  ylim(0, 20000)+
  geom_text(aes(label=Frequency),hjust=0, angle = 90, vjust=0,size=2.5) +
  theme(axis.text.x = element_text(angle = 90, hjust = 1))

pName = "E:\\TestDavidSolutions\\newGridSize\\Toy0\\FitnessDistribution.jpg"

ggsave(pName, plot=p, height = 6, width = 12, units = "in")


temp = test[,c(2:3)]
temp$V51 <- as.character(temp$V51)
temp$V53 <- as.numeric(temp$V53)

p <- ggplot(data = temp, aes(x = as.factor(V51), y = V53))+
  geom_point(color="darkblue") +
  xlab("Leaks") +
  ylab("Area") +
  theme_minimal()
ggsave("E:\\TestDavidSolutions\\newGridSize\\Toy\\AreaLeakDistributionToy.jpg", 
       p , height = 7, width = 10)

