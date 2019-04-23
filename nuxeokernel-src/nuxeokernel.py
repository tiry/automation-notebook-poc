from ipykernel.kernelbase import Kernel
from nuxeo.client import Nuxeo

class NuxeoKernel(Kernel):
    implementation = 'Nuxeo'
    implementation_version = '1.0'
    language = 'no-op'
    language_version = '0.1'
    language_info = {
        'name': 'Any text',
        'mimetype': 'text/plain',
        'file_extension': '.txt',
    }
    banner = "Nuxeo kernel - run automation scripts"    

    def __init__(self, **kwargs):
        super(NuxeoKernel, self).__init__(**kwargs)
        self.log.info("Nuxeo client initailized")

    def start(self):
        super(NuxeoKernel, self).start()
        self.nuxeo = Nuxeo(host='http://demo.nuxeo.com/nuxeo/',auth=('Administrator', 'Administrator'))    
        self.log.error("Nuxeo client started")

    def do_execute(self, code, silent, store_history=True, user_expressions=None,
                   allow_stdin=False):

        # message format: https://github.com/jupyter/jupyter_client/blob/master/jupyter_client/session.py#L657
        # https://jupyter-client.readthedocs.io/en/stable/messaging.html
        if not silent:
            doc = self.nuxeo.documents.get(path=code)            
            #stream_content = {'name': 'stdout', 'text': doc.properties}
            stream_content = {'name': 'stdout', 'text': code + doc.uid}
            #self.send_response(self.iopub_socket, 'stream', stream_content)
            
            display_content = {'data': { \
                                        'application/json': {'uid' : doc.uid},\
                                        'text/plain': "Youhou",\
                                        'text/html': "<b>You</b>hou",\
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
