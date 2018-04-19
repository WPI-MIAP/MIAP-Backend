#!/usr/bin/python2
import sys
import io

def main(file):
    """This program reads in a database file output by RxNorm, and parses it
    into a json database for use in MARAS."""
    output_dict = dict()
    line = str()
    i = 0
    
    with io.open(file) as reader:
        while True:
            line = reader.readline()
            if not line:
                break
            row = line.split('|')
            nId = int(row[8])
            name = str(row[14].lower())
            full_name = full_name_process(name)

            name = process(name)
            name = ''.join(name.split())
            if len(name) < 4:
                continue
            # if len(name) < 5: print (name)
            flag = True
            for drug_id in output_dict.keys():
                drug = output_dict[drug_id][0]
                if drug in name:
                    flag = False
                    break;
                if name in drug:
                    output_dict.pop(drug_id)
            if flag:
                output_dict[nId] = (name,full_name)

            i += 1
            if (i % 1000) == 0: # Keep track of progress. This program takes nearly an hour.
                print('Line #' + str(i) + ' parsed.')
            # if i == 20000: break
                    
            
    with io.open('/wpi/MARAS/test2.json', mode='w+') as writer: #Start to write the output
        writer.write(u'{"Drugs" :[\n')
        for drug_id in sorted(output_dict.keys()):
            writer.write(u'  {\n    ')
            writer.write(u'"ID" : ' + str(drug_id) + ',\n    ')
            writer.write(u'"short_name" : "' + output_dict[drug_id][0] + u'",\n    ')
            writer.write(u'"full_name" : "' + output_dict[drug_id][1] + u'"\n  },\n')
        
        writer.write(u']}\n')
        writer.flush()

words_to_remove = ['pill', 'oral', 'tablet', 'injection', 'intravenus', 'capsule', 'intravenous',
'once-daily', 'once', 'daily', 'twice', ' mg', '.', '%', ' ml', '/mg', '/ml', 'lotion', 'ointment', 'topical',
'vaginal', 'solution', 'soap', 'liquid' , 'injectable', 'per', 'menstrual', 'pain', 'as ', 'chewable', 'license',
'holder', 'unspecified', ' in ', 'coated', 'with', 'cream', 'intramuscular', 'powder', 'for ', 'ophthalmic', 'extended',
'release', ' gm', '/gm', 'gram', 'milligram', '#', ' g ', '/g ', 'film', 'gelatine', 'gelatin', 'gelat', 'subcutaneous',
 'cutaneous', 'drug', 'implant', 'childrens', ' kit', 'gel']
words_to_remove += [str(x) for x in range(0,10)]

def process(name):
    if name.find('[') != -1:
        name = name[name.find('[')+1:name.find(']')]
    # if name.find('(') != -1:
    #     name = name[name.find('(')+1:name.find(')')]
    for pattern in words_to_remove:
        while name.find(pattern) != -1:
            name = name[:name.find(pattern)] + name[name.find(pattern)+len(pattern):]
    return name

def full_name_process(name):
    if name.find('[') != -1:
        name = name[name.find('[')+1:name.find(']')]
    return name
if __name__ == '__main__':
    
    main(sys.argv[1])