from ipykernel.kernelbase import Kernel
from nuxeo.client import Nuxeo

class NuxeoKernel(Kernel):
    implementation = 'Nuxeo'
    implementation_version = '1.0'
    language = 'javascript'
    language_info = {
        'name': 'javascript',
        'mimetype': 'text/javascript',
        'file_extension': '.js',
        '__codemirror_mode': {"name": "javascript", "json": True}
    }
    banner = "Nuxeo kernel - run automation scripts"    

    def __init__(self, **kwargs):
        super(NuxeoKernel, self).__init__(**kwargs)
        self.log.info("Nuxeo client initailized")

    def start(self):
        super(NuxeoKernel, self).start()
        self.nuxeo = Nuxeo(host='http://127.0.0.1:8080/nuxeo/',auth=('Administrator', 'Administrator'))    
        self.log.error("Nuxeo client started")

    def nx_Exec(self, code):
        operation = self.nuxeo.operations.new('Automation.KernelExecutor')
        operation.input_obj = code
        return operation.execute().get('value')

    def nx_Autocomplete(self, code, prefix):
        operation = self.nuxeo.operations.new('Automation.KernelAutocomplete')
        operation.params = {'prefix': prefix}
        operation.input_obj = code
        return operation.execute().get('value')

    def _clean_prefix(self,prefix):

        for sep in [' ', '(', ')', '[', ']', '{', '}', ':', ';']:
            idx = prefix.rfind(sep)
            if (idx>=0):
                prefix=prefix[idx+1:]    
        return prefix


    def do_complete(self, code, cursor_pos):

        line = code[:cursor_pos]
        start_idx=line.rfind('\n')
        line_prefix=line[start_idx+1:]

        prefix = self._clean_prefix(line_prefix)

        self.log.error("Complete request:" + prefix)

        cursor_start = cursor_pos - (len(line)-start_idx) + (len(line_prefix) - len(prefix))

        suggestions = self.nx_Autocomplete(code, prefix)

        #matches = [prefix+'x', prefix+'y', prefix + 'z']
        matches = suggestions.split("\n")

        complete_reply = {'matches': matches, 'cursor_start' : cursor_start ,  'cursor_end' : cursor_pos, 'status':'ok'}
        #content = {'matches' : matches, 'cursor_start' : low_cp, 
        #           'cursor_end' : cursor_pos, 'metadata' : {}, 'status' : 'ok'}
        ##self.send_response(self.iopub_socket, 'complete_reply', complete_reply)
        return complete_reply

    def do_execute(self, code, silent, store_history=True, user_expressions=None,
                   allow_stdin=False):

        # message format: https://github.com/jupyter/jupyter_client/blob/master/jupyter_client/session.py#L657
        # https://jupyter-client.readthedocs.io/en/stable/messaging.html
        if not silent:

            html = self.nx_Exec(code)

            #stream_content = {'name': 'stdout', 'text': doc.properties}
            #stream_content = {'name': 'stdout', 'text': code + doc.uid}
            #self.send_response(self.iopub_socket, 'stream', stream_content)
            
            display_content = {'data': { \
                                        'text/html': html,\
                                         }, 'metadata' : { 'application/json' : { 'expanded': True } }}            
            self.send_response(self.iopub_socket, 'display_data', display_content)


        return {'status': 'ok',
                # The base class increments the execution count
                'execution_count': self.execution_count,
                'payload': [],
                'user_expressions': {},
               }

if __name__ == '__main__':
    from ipykernel.kernelapp import IPKernelApp
    IPKernelApp.launch_instance(kernel_class=NuxeoKernel)
