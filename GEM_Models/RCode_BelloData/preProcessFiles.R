setwd("D:/BellosData/Goodbadsplit_31042017")

library(stringr)
library(reshape2)

#get the files
rawFiles = list.files(path = "D:/BellosData/Goodbadsplit_31042017")

#iterate through the list of files
for(i in 1:length(rawFiles)){
  
  currentFile = rawFiles[i]
  
  rawData = readLines(currentFile)
  
  ######### -------- create the new file name with generation no -------- #########
  
  #get the generation no from the second row
  generationNO = substr(rawData[2],13,nchar(rawData[2])) 
  
  #convert it to a 3 digit no
  if(nchar(generationNO) == 1){
    generationNO = paste("00",generationNO,sep = "")
  }else if (nchar(generationNO) == 2){
    generationNO = paste("0",generationNO,sep = "")
  }else{}
  
  #create new file name
  #fileName = currentFile
  newFileName = paste(substr(currentFile,1,nchar(currentFile) - 2),"_gen_" ,generationNO,".csv", sep="")
  
  
  ######### -------- create data for the new file -------- #########
  
  # remove the first 2 rows
  newData = rawData[-(1:2)]
  
  #convert char vector into dataframe
  tempNewData = data.frame(do.call(rbind, strsplit(newData, "\n", fixed=TRUE)))
  
  #remove the square bracket from each row
  tnd = apply(tempNewData, 1, function(x) gsub(".*\\[(.*)\\].*", "\\1", x))
  
  #convert into dataframe
  newDataFile = colsplit(tnd, ",", names = c(paste0("C",seq(1:length(strsplit(tnd[1],",")[[1]])))))
  
  #setting the model name column
  newDataFile = cbind(ModelName = c(sprintf("Gen_%s_M_%03d",generationNO,1:nrow(newDataFile))),newDataFile)
  
  
  write.csv(newDataFile, file = newFileName, quote = F, row.names = F)
  
  rawData <- NULL
  newData <- NULL
  newDataFile <- NULL
  
  
}