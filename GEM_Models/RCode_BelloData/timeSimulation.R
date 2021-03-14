setwd("D:/BellosData/GC_3")
library(stringr)

generationFiles = list.files(pattern = "^Generation.*\\_\\d+.txt$")

start.time = Sys.time()
modelSampleSize = 50

for(a in 1:length(generationFiles)){
#for(a in 1:1){#length(generationFiles)){
  
  fname = generationFiles[a]
  
  geneData = read.table(generationFiles[a], sep = "\n", header = T, stringsAsFactors = F)
  
  geneCount = str_count(geneData[1,1],"[1|0]")
  
  geneDF <- data.frame(Gene = paste0("Gene_", c(1:geneCount)), stringsAsFactors = F)
  
  
  for(i in 1:modelSampleSize){#nrow(geneData)){    ##### limit the no of models
    
    #remove the square brackets
    dataRow = substring(geneData[i,1], 2, nchar(geneData[i,1]) - 1)
    
    #removing white spaces
    dataRow = gsub(" ", "", dataRow, fixed = T)
    
    #splitting the string 
    dataRow = strsplit(dataRow, ",")
    
    #converting to numeric
    dataRow = as.numeric(unlist(dataRow))
    
    colName = paste("M_",i,sep = "")
    geneDF <- cbind(geneDF, dataRow)
    colnames(geneDF)[colnames(geneDF) == 'dataRow'] <- colName
    
    dataRow <- NULL
  }
  
  summaryDF <- data.frame(Model1 = character(), Model2 = character(), Common = integer(),
                          Uncommon = integer(), Absent = integer(), stringsAsFactors = F)
  
  modelNames = paste0("M_",c(1:modelSampleSize))
  
  secondaryLevel = modelNames
  
  fileName = paste0("D://BellosData//GC_3//TimeTrials//",
                    substr(fname, 1, nchar(fname) - 4),"_TimeTrials_MS_",modelSampleSize,"_Summary.txt")
  
  stop = modelSampleSize - 1
  
  for(x in 1:stop){
    
    secondaryLevel = secondaryLevel[-1]
    
    for(y in 1:length(secondaryLevel)){
      
      result = geneDF[modelNames[x]] + geneDF[secondaryLevel[y]]
      
      summaryDF <- rbind(summaryDF, data.frame(Model1 = modelNames[x],
                                               Model2 = secondaryLevel[y],
                                               Common = sum(result == 2),
                                               Uncommon = sum(result == 1),
                                               Absent = sum(result == 0)))
      
    }
    
    
  }
  
  write.table(summaryDF, fileName, sep = ",", quote = F, row.names = F)
  geneDF <- NULL
  summaryDF <- NULL
  geneData <-NULL
  
}

end.time = Sys.time()
tot = end.time - start.time