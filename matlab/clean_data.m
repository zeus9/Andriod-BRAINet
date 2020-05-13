rootDir = dir("C:\Users\ryane\GoglandProjects\playground\eeg_proj\*");
for file = rootDir'
    if isempty(strfind(file.name, "S")) == false
        cur_files = dir(file.folder + "\" + file.name + "\*.edf");
        mkdir(file.folder + "\clean_" + file.name)

        for subfile = cur_files'
           [hdr,record] = edfread(file.folder + "\" + file.name + "\" + subfile.name);
           zero_mean_unit_variance = (record - mean(record(:))) ./ var(record(:));
           [filepath, name, ext] = fileparts(subfile.name);
           writematrix(zero_mean_unit_variance, file.folder + "\clean_"+file.name+"\"+name + ".csv")
        end
    end
end

