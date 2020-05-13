rootDir = dir("/Users/jk/dev/CSE535-BraiNetProject/matlab/all_clean/");
for file = rootDir'
    if isempty(strfind(file.name, "S")) == false
        cur_files = dir(file.folder + "/" + file.name + "/*.csv");
        mkdir(file.folder + "/freq_" + file.name)

        for subfile = cur_files'
            filepath = file.folder + "/" + file.name + "/" + subfile.name;
            A = readmatrix(filepath);
            f = abs(fftshift(fft2(A')));
            % save
            disp("Saving to " + file.folder + "/freq_" + file.name + "/" + subfile.name);
            writematrix(f, file.folder + "/freq_" + file.name + "/" + subfile.name);
        end
    end
end

