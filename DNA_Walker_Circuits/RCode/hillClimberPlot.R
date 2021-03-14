library(ggplot2)
library(reshape2)

folderName = "E:\\HillClimber\\NewGridSize\\Toy\\"

masterRunData = data.frame(Iteration = 1:150000)

for(i in 1:2){
  
  file = paste0(folderName,"Run",i,"\\BestSolutionsLog.txt")
  
  runData <- read.table(file, header = T, stringsAsFactors = F)  
  runData <- runData[-c(1),]
  
   # if(nrow(runData) > 40000){
   # 
   #   runData <- runData[-c(40001:nrow(runData)),]
   # 
   # }
  
  cn = paste0("Run",i)
  
  masterRunData[cn] <- unlist(runData$Elitef)
  
  runData <- NULL
  
}

t = masterRunData[masterRunData$Run2 == min(masterRunData$Run2),]



masterRunData_long <- melt(masterRunData, id="Iteration")

masterRunData_long_Temp <- masterRunData_long[(masterRunData_long$Iteration > 4500) & (masterRunData_long$Iteration < 12001),]


p <- ggplot(data=masterRunData_long_Temp,
       aes(x=Iteration, y=value, colour=variable)) +
  geom_line(show.legend = FALSE) +
  ggtitle("Model Toy - Best Fitness Covergencce Plot (150000 iterations - 40 runs)") +
  xlab("Iterations (4500 - 12000)") +
  # xlab("Iterations") +
  ylab("Best Fitness") +
  #theme(legend.position = "none") +
  theme_minimal()

pName = paste0(folderName,"convergencePlot_Toy_40_crossSection.jpg")
# pName = paste0(folderName,"convergencePlot_Toy1_30.jpg")
ggsave(pName, p, height = 7, width = 10)