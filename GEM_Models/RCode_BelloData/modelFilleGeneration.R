# convert reaction data file to a table (Reaction, Activity) and save it

setwd("D:/BellosData/GF_3")
library(stringr)

reactionDataFiles = gtools::mixedsort(list.files(pattern = "^Generation_Flux_.*.txt$"))

for(x in 1:length(reactionDataFiles)){
 
  genfname = substr(reactionDataFiles[x],1,nchar(reactionDataFiles[x]) - 4)
  
  reactionData = read.table(reactionDataFiles[x], sep = "\n", stringsAsFactors = F)
  
  startingPositions = grep("\\[",reactionData[,1])
  endingPositions = grep("\\]",reactionData[,1])
  
  modelReactions = data.frame(Reaction = character(), Activity = numeric(), stringsAsFactors = F)
  
  for(i in 1:length(startingPositions)){

    start = startingPositions[i] + 1
    end = endingPositions[i] - 1
    
    tempData =  as.data.frame(reactionData[c(start:end),1])
    
    modelNo = str_extract(reactionData[startingPositions[i],1],"\\d+")
    
    fileName = paste0("D:\\BellosData\\GF_3\\Models\\",genfname,"_Model_",modelNo,"_raw.txt")
    
    
    if(start<end){
      
      for(j in 1:nrow(tempData)){
        
        datum = unlist(str_split(unlist(tempData[j,1]),":"))
        
        modelReactions = rbind(modelReactions, data.frame(Reaction = datum[1],
                                                          Activity = as.numeric(trimws(datum[2],"l"))))
        
      }
      
    }else{
      
      modelReactions = rbind(modelReactions, data.frame(Reaction = "",
                                                        Activity = ""))
      
      
    }
    
    modelReactions$Activity <- as.numeric(as.character(modelReactions$Activity))
    write.table(modelReactions, fileName, sep = ",", quote = F, row.names = F)
    
    #convert to absolute values and round the reactions
    
    fileName2 = paste0("D:\\BellosData\\GF_3\\Models\\",genfname,"_Model_",modelNo,".txt")
    
    modelReactions$Activity =  unlist(lapply(modelReactions$Activity,function(x) abs(x)))
    modelReactions$Activity =  unlist(lapply(modelReactions$Activity,function(x) round(x, digits = 5)))
    
    write.table(modelReactions, fileName2, sep = ",", quote = F, row.names = F)
    
    modelReactions <- NULL
    tempData <- NULL
  }
  
  reactionData <- NULL
  
}




