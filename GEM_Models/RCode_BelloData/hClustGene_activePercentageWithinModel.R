setwd("D:/BellosData/GC_3/Preprocessed")

getGens <- function(v) {
  pat <- paste0("(", paste0(v, collapse="|"), ")")
  return(pat)
}

geneActivePercentage <- function(model){
  
  activity = round(sum(model == 1)/139,digits = 5)
  return(activity)
}

computeGeneActivePercentage <- function(fileName){
  
  generationNo = str_extract(fileName, "\\d+")
  
  rawData = read.table(fileName, sep = ",", header = T, stringsAsFactors = F)
  
  rawData <- as.data.frame(apply(rawData, 2, as.character))
  
  rawDataT <- as.data.frame(apply(rawData[,2:ncol(rawData)], 2, as.numeric))
  
  activityDF <- as.data.frame(apply(rawDataT, 2, function(x) geneActivePercentage(x)))
  
  activityDF <- cbind(rownames(activityDF),activityDF)
  
  colnames(activityDF) <- c("Model", "ActivePercent")
  
  write.table(activityDF, 
              paste0("D:/BellosData/GC_3/Clustering_activePercentage/Gen_",generationNo,"_activePercentage.txt"), 
              sep = ",", quote = F, row.names = F)
  
  hcN <- hclust(dist(activityDF))
  hcdN <-as.dendrogram(hcN)
  png(paste0("D:/BellosData/GC_3/Clustering_activePercentage/Gen_",generationNo,"_ActivePercent_hclust.png"), height = 700, width = 1200)
  plot(hcdN, xlab = "Height", horiz = F , 
       main = paste0("Clustering by active gene percentage (interior) - Generation ",generationNo))
  dev.off()
  
}
  

sTime = Sys.time()

gens <- c(0:249)#, 49, 99, 149, 199, 249)      # or any values you wish to use
# gens <- c(0,10,20, 30,  40,50,60, 70, 80,  90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200,
#           210, 220, 230, 240, 249)
pat <- paste0("^Generation_Chromosones_", getGens(gens), "_DF.txt$")

generationFiles = gtools::mixedsort(list.files(pattern = pat))

lapply(generationFiles, function(x) computeGeneActivePercentage(x))

eTime = Sys.time()
tTime = eTime - sTime
