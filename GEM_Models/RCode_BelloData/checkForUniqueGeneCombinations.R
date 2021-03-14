# compare unique solutions of genes in each generation

setwd("D:/BellosData/GC_3/Preprocessed")

geneDataFiles = gtools::mixedsort(list.files(pattern = "^.*.txt$"))

duplicates = data.frame(Generation = integer(), NoOfDuplicates = integer(), stringsAsFactors = F)

for(i in 1:length(geneDataFiles)){
  
  #generation no
  generationNum = as.numeric(str_extract(geneDataFiles[i], "\\d+"))
  
  # transpose the data frame
  tempData = read.table(geneDataFiles[i], sep = ",", header = F)
  tempDataT = t(tempData)
  colnames(tempDataT)<-tempDataT[1,]
  tempDataT = tempDataT[-1,]
  #tempDataT = cbind(ModelName = row.names(tempDataT), tempDataT)
  colnames(tempDataT)[1] <- "Model"
  
  #find whether combinations are duplicated
  isDuplicated = duplicated(tempDataT)
  
  noOfDuplicates = length(isDuplicated[isDuplicated == TRUE])
  
  # if(noOfDuplicates > 0){
  #   
  #   duplicates = which(isDuplicated, arr.ind = TRUE)
  #   
  # }else{
  #   
  # }
  
  duplicates = rbind(duplicates, data.frame(Generation = generationNum,
                                            NoOfDuplicates = noOfDuplicates))
  
}

duplicates = duplicates[with(duplicates, order(Generation)), ]

write.table(duplicates, "GeneSeqDuplicates.txt", sep = ",", quote = F, row.names = F)




# a = geneData[1,1]
# b = geneData[2,1]
# c = a
# 
# all(b==b)
# 
# unique(a,b,c)
# 
# levels(geneData)
# duplicated(geneDF)
# 
# a <- c(rep("A", 3), rep("B", 3), rep("C",2))
# b <- c(1,1,2,4,1,1,2,2)
# c <- c(1,1,2,4,1,2,2,2)
# df1 <-data.frame(a,b,c)
# 
# b = duplicated(df1)
# 
# length(b[b==T])
# 
# which(b, arr.ind = T)
# 
