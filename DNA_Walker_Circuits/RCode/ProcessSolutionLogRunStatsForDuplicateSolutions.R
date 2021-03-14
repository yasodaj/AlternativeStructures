#process solutions log in each Run folder to find out the duplicate solutions
#duplicate solutins is found using java CountDuplicateSOlutionsMaster.java


for(i in 1:100){
 
  fileName = paste0("E:\\SA\\Toy0_SA\\Run",i,"\\BestSolutionsLog.txt")
  
  solutionsLogData<-read.table(fileName, header = T, colClasses = rep("character",8),
                               row.names = NULL, sep = "\t")
  solutionsLogData$Solution <- lapply(solutionsLogData$Solution, function(x) gsub("\\[","",x))
  solutionsLogData$Solution <- lapply(solutionsLogData$Solution, function(x) gsub("\\]","",x))
  
  writeFileName = paste0("E:\\SA\\Toy0_SA\\RunStats\\BestSolutionLog\\BestSolutionsLogRun",i,".txt")
  
  write.table(solutionsLogData$Solution,writeFileName, row.names = F, quote = F,
              col.names = F, sep = "\n")
  
  
}
 