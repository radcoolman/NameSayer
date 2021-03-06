package data;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will contain information about the original database, the current playlist and the names to be displayed.
 */
public class NameSayerModel {

    private List<Name> _database = new ArrayList<Name>();
    private List<Name> _nameslist = new ArrayList<Name>();
    private List<Name> _filteredNameslist = new ArrayList<Name>();
    private List<Playlist> _filteredPlaylistList = new ArrayList<Playlist>();
    private Playlist _playlist;
    private List<Playlist> _playlists = new ArrayList<>();
    private final File GOODNAMES = new File("userdata/goodNames.txt");
    private final File BADNAMES = new File("userdata/badNames.txt");


    public NameSayerModel(){
        // making  the directories for userdata if they don't exist
        File playlistDir = new File("userdata/playlists");
        playlistDir.mkdirs();

        // load the database from the folder
        File namesData = new File("names");
        if(!namesData.exists()){
            namesData.mkdirs();
        }
        List<File> listOfNamesData = Arrays.asList(namesData.listFiles());
        List<String> readNames = new ArrayList<>();

        // For every name this finds all names that match it to make a list of files for the Name constructor
        // get number of files, n. while i < n, read listofnamesdata(0)
        for (File file1 : listOfNamesData) {
            List<File> database = new ArrayList<>();
            // adding the first database audio file
            database.add(file1);
            // making a fixed version if it doesn't already exist
            if (!new File("userdata/fixed/fix" + file1.getName()).exists()) {
                FileCommands.removeSilence(file1);
            }
            String name;
            // files end in "_[name].wav"
            Pattern p = Pattern.compile("_([a-zA-Z]*)\\.wav");
            Matcher m1 = p.matcher(file1.getName());
            if (m1.find()) {
                name = m1.group(1);
                // checking if this name has already been done (case insensitive)
                if (!readNames.contains(name.toLowerCase())) {
                    readNames.add(name.toLowerCase());
                    for (File file2 : listOfNamesData) {
                        Matcher m2 = p.matcher(file2.getName());
                        if (m2.find() && !file2.equals(file1)) {
                            if (m2.group(1).toLowerCase().equals(name.toLowerCase())) {
                                database.add(file2);
                            }
                        }
                    }
                    _database.add(new Name(name, database, GOODNAMES, BADNAMES));
                }
            } else {
                // This will not have any other files with same name, as the name is the file name
                name = file1.getName();
                _database.add(new Name(name, database, GOODNAMES, BADNAMES));
            }

        }
        _database.sort(new Comparator<Name>() {
            @Override
            public int compare(Name o1, Name o2) {
                return (o1.compareTo(o2));
            }
        });


        if (playlistDir.isDirectory()) {
            for (File playlist: playlistDir.listFiles()) {
                _playlists.add(new Playlist(playlist,this));
            }
        }
        _nameslist.addAll(_database);
    }

    // Getters
    public List<Name> getDatabase(){
        return _database;
    }
    public Playlist getPlaylist(){
        return _playlist;
    }
    public List<Playlist> getPlaylists() { return _playlists; }
    public List<Name> getFilteredNamesList(){
        return _filteredNameslist;
    }
    public List<Playlist> getFilteredPlaylistList(){
        return _filteredPlaylistList;
    }

    /**
     * Set the playlist to be referenced by the play screen and edit screen.
     */
    public void setPlaylist(Playlist playlist) {
        _playlist = playlist;
    }

    /**
     * Deletes a playlist and the attempts associated with it, unless the attempt is also part of another playlist.
     * This way if an attempt is no longer in use it will be deleted
     */
    public void deletePlaylist(Playlist playlist) {
        _playlists.remove(playlist);
        FileCommands.deleteFile(playlist.getFile());
        List<File> allAttempts = new ArrayList<>();
        for (Playlist p: _playlists) {
            for (FullName f: p.getFullNames()) {
                allAttempts.addAll(f.getAttempts());
            }
        }
        for (FullName fullName: playlist.getFullNames()) {
            for (File attempt: fullName.getAttempts()) {
                if (!allAttempts.contains(attempt)) {
                    FileCommands.deleteFile(attempt);
                }
            }
        }
    }

    /**
     * This method will place all the names that contain the string filter in the _filteredNameslist;
     * @param filter
     */
    public void filterNamesList(String filter){
        _filteredNameslist = new ArrayList<Name>();

        // Add all names that contain the text from the search bar
        for(Name n: _nameslist){
            if(n.toString().toLowerCase().contains(filter.toLowerCase())){
                _filteredNameslist.add(n);
            }
        }

        // Sort with priority of the search bar text appearing earlier in the name.
        _filteredNameslist.sort(new Comparator<Name>() {
            @Override
            public int compare(Name o1, Name o2) {
                String i = o1.toString().toLowerCase().indexOf(filter.toLowerCase())+"";
                String j = o2.toString().toLowerCase().indexOf(filter.toLowerCase())+"";
                return (i.compareTo(j));
            }
        });
    }

    /**
     * This method will place all the names that contain the string filter in the _filteredPlaylistList;
     * @param filter
     */
    public void filterPlaylistList(String filter){
        _filteredPlaylistList = new ArrayList<Playlist>();

        // Add all names that contain the text from the search bar
        for(Playlist p: _playlists){
            if(p.toString().toLowerCase().contains(filter.toLowerCase())){
                _filteredPlaylistList.add(p);
            }
        }

        // Sort with priority of the search bar text appearing earlier in the name.
        _filteredPlaylistList.sort(new Comparator<Playlist>() {
            @Override
            public int compare(Playlist o1, Playlist o2) {
                String i = o1.toString().toLowerCase().indexOf(filter.toLowerCase())+"";
                String j = o2.toString().toLowerCase().indexOf(filter.toLowerCase())+"";
                return (i.compareTo(j));
            }
        });
    }

    /**
     * This method will update the good and bad names txt files
     */
    public void writeGoodBadNames(){
        BufferedWriter goodWriter;
        BufferedWriter badWriter;
        try{
            // deleting the old files and making the new ones
            FileCommands.deleteFile(GOODNAMES);
            FileCommands.deleteFile(BADNAMES);
            goodWriter = new BufferedWriter(new FileWriter(GOODNAMES, true));
            badWriter = new BufferedWriter(new FileWriter(BADNAMES, true));
            for(Name n: _database){
                for(File f: n.getFiles()){
                    //writing all good files to text file
                    if (n.getRating(f) == 2) {
                        goodWriter.write(f.getPath()+"\n");
                    }
                    //writing all bad files to text file
                    if (n.getRating(f) == 1) {
                        badWriter.write(f.getPath()+"\n");
                    }
                }
            }
            badWriter.close();
            goodWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
