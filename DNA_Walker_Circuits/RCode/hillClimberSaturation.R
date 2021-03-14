folderName = "E:\\HillClimber\\NewGridSize\\Toy\\"

test = data.frame(Run = numeric(), lowestFit = numeric(), 
                  firstObservedAt = numeric(), stringsAsFactors = F)

for(i in 1:40){
  
  file = paste0(folderName,"Run",i,"\\BestSolutionsLog.txt")
  
  runData <- read.table(file, header = T, stringsAsFactors = F)  
  runData <- runData[-c(1),]
  
  # if(nrow(runData) > 40000){
  # 
  #   runData <- runData[-c(40001:nrow(runData)),]
  # 
  # }
  
  
  lowestFitness = min(runData$Elitef)
  fOA = min(runData[runData$Elitef == unlist(lowestFitness),][,1])
  test = rbind(test, data.frame(Run = i, lowestFit = lowestFitness, 
                                firstObservedAt = fOA))
  
}

newdata <- test[order(-test$firstObservedAt),] 

write.table(newdata,paste0(folderName,"40RunSummary.txt"),quote = F, row.names = F,
            sep = ",")
