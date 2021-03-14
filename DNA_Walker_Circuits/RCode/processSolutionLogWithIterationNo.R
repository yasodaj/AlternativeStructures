
library(data.table)

datInit <- read.table("E:\\SA\\Toy\\NewGridSize\\Run4\\SolutionsLog.txt", 
                  header = T, stringsAsFactors = F)


initSolutions = datInit[datInit$FileName %like% "SA_Init_Iteration_",]



#unique(initSolutions$Solution)

#as.numeric(gsub(".*_(\\d+).*$", "\\1", x))

initSolutions["IterationNo"] <- 0
stringProcessFunction <- function(x) as.numeric(gsub(".*_(\\d+).*$", "\\1", x))
initSolutions$IterationNo <- mapply(stringProcessFunction, initSolutions$FileName)

write.table(initSolutions, "E:\\SA\\Toy\\NewGridSize\\Run4\\FilteredInitSolutions.txt", quote = F, row.names = F, sep = "\t")

#sections

totSec = round(nrow(initSolutions)/100)

start = 1

for(sec in 1:totSec){

  end = start + 99
  
  if(end > nrow(initSolutions)){
    
    end = nrow(initSolutions)
    
  }
  
  temp = initSolutions[c(start:end),]
  
  fileName = paste0("E:\\LayoutComparison\\Toy\\RMSDWithRotation\\Sample4\\InitSolutionsClustering\\100\\FilteredInitSolutions_100_",sec,"_of_",totSec,".txt")
  
  write.table(temp,fileName , quote = F, row.names = F, sep = "\t")
  
  start = end + 1


}

#sliding window
totSec = 9

start = 1

for(sec in 1:totSec){
  
  if(sec == 1){
    end = start + 499
  } else{
    end = start + 499 + 1
  }
  
  if(end > nrow(initSolutions)){
    
    end = nrow(initSolutions)
    
  }
  
  temp = initSolutions[c(start:end),]
  
  fileName = paste0("E:\\SA\\Toy\\NewGridSize\\Run2\\FilteredInitSolutionsOverlappingSections_",sec,"_of_",totSec,".txt")
  
  write.table(temp,fileName , quote = F, row.names = F, sep = "\t")
  
  start = end - 250
  
  
}


#unique solutions and their count

uniquInitSolutions = as.data.frame(table(initSolutions$Solution))

uniquInitSolutions$IterationIDs <- 0

for(i in 1:nrow(uniquInitSolutions)){
  
  s = as.character(uniquInitSolutions[i,1])
  #rowNumbers = which(datInitNew$Solution == s, arr.ind = TRUE)
  rowNumbers = initSolutions[initSolutions$Solution == s,]$IterationNo
  uniquInitSolutions[i,3] <- paste(as.character(rowNumbers), collapse=", ")
  
}

uniquInitSolutions$Fitness <- 0

for(i in 1:nrow(uniquInitSolutions)){
  
  s = as.character(uniquInitSolutions[i,1])
  fitness = initSolutions[initSolutions$Solution == s,7][1]
  uniquInitSolutions[i,4] <- fitness
  
}

uniquInitSolutions[uniquInitSolutions$Fitness == 54,]

write.table(uniquInitSolutions, "E:\\SA\\Toy\\NewGridSize\\Run4\\UniqueInitSolutions.txt", quote = F, row.names = F, sep = "\t")

plot(initSolutions$IterationNo, initSolutions$Fitness)
