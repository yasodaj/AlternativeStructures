#pre-process data - convert binary gene string to table

setwd("D:/BellosData/GC_3")
library(stringr)
library(gtools)

generationFiles = list.files(pattern = "^Generation.*\\_\\d+.txt$")
generationFiles = mixedsort(generationFiles)


for(a in 1:length(generationFiles)){
  #for(a in 1:1){#length(generationFiles)){
  
  fname = generationFiles[a]
  
  geneData = read.table(generationFiles[a], sep = "\n", header = T, stringsAsFactors = F)
  
  geneCount = str_count(geneData[1,1],"[1|0]")
  
  geneDF <- data.frame(Gene = paste0("Gene_", c(1:geneCount)), stringsAsFactors = F)
  
  
  for(i in 1:nrow(geneData)){    ##### limit the no of models
    
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
  
  geneDF[1,450]
  
  
  fileName = paste0("D://BellosData//GC_3//Preprocessed//",
                    substr(fname, 1, nchar(fname) - 4), "_DF.txt")
  
  
  write.table(geneDF, fileName, sep = ",", quote = F, row.names = F)
  geneDF <- NULL
  geneData <- NULL
  
}
  
  

  
