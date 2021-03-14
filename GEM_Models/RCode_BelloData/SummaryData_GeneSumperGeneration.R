setwd("D:/BellosData/Goodbadsplit_31042017")


#get the files
rawFiles = list.files(path = "D:/BellosData/Goodbadsplit_31042017", pattern = "^bad.*.csv$")

dataTable = data.frame("Gene" = paste0("C",seq(1:139)))

dt = data.frame()

#iterate through the list of files
for(i in 1:length(rawFiles)){
  
  currentFile = rawFiles[i]
  
  data = read.csv(currentFile)
  
  genNo = substr(data[1,1],1,7)
  
  dataTable[,genNo] <- colSums(data[2:140])
  
  dt = rbind(dt, data.frame(Model = genNo,Gene = colnames(data)[-1] ,GeneSum = colSums(data[2:140])))
  
}

write.table(dataTable, "MasterSummary_BadGeneration.txt",sep = ",", quote = F, row.names = F)
write.table(dt, "MasterSummary_BadGeneration_Transpose.txt",sep = ",", quote = F, row.names = F)
dataTable <- NULL
dt<-NULL