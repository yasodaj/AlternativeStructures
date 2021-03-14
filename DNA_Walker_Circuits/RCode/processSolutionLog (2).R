#process for java duplicate count

parentDirectory = "E:\\SA\\Toy\\NewGridSize_WithAlternativeSolutions\\"

for(i in 1:20){
 
  fname = paste0(parentDirectory,"Run", i,"\\SolutionsLog.txt")
  
  d = read.table(fname, header = T, stringsAsFactors = F, sep = "\t")
  
  #d[,2] <- lapply(d[2], function(x) substr(x,2,28)) #2nd column solution string 2 - 24
  
  writeName = paste0(parentDirectory,"RunStats\\SolutionLogs\\SolutionsLogRun",i,".txt")
  
  write.table(d[,2], file = writeName , sep = "\n", quote = F, row.names = F, 
              col.names = F)
  d<-NULL
}




for(i in 1:20){
  
  fname = paste0(parentDirectory,"Run", i,"\\BestSolutionsLog.txt")
  
  d = read.table(fname, header = T, stringsAsFactors = F, sep = "\t")
  
  #d[,4] <- lapply(d[4], function(x) substr(x,2,28)) #2nd column solution string 2 - 24
  
  writeName = paste0(parentDirectory,"RunStats\\BestSolutionLogs\\BestSolutionsLogRun",i,".txt")
  
  write.table(d[,4], file = writeName , sep = "\n", quote = F, row.names = F, 
              col.names = F)
  d<-NULL
}